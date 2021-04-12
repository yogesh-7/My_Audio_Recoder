package com.example.myaudiorecoder.Ui.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.example.myaudiorecoder.R;
import com.example.myaudiorecoder.databinding.FragmentAudioListBinding;
import com.example.myaudiorecoder.databinding.FragmentRecordBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordFragment extends Fragment {


    private FragmentRecordBinding binding;
    private NavController navController;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 01;
    private String TAG = "RecordFragment";

    private MediaRecorder mediaRecorder;
    private String recordFile;

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();//onStop
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.recordListBtn.setOnClickListener((View v) -> {
            if(isRecording){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                        isRecording = false;
                    }
                });
                alertDialog.setNegativeButton("CANCEL", null);
                alertDialog.setTitle("Audio Still recording");
                alertDialog.setMessage("Are you sure, you want to stop the recording?");
                alertDialog.create().show();
            }else {
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
            }

        });

        binding.recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    //stop Recording
                    stopRecording();
                    isRecording = false;
                    if(getContext()!=null)
                    binding.recordBtn.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.record_btn_stopped, getContext().getTheme()));
                } else {
                    if (checkPermissions()) {
                        //start Recording
                        startRecording();
                        isRecording = true;
                        if(getContext()!=null)
                        binding.recordBtn.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.record_btn_recording, getContext().getTheme()));
                    }


                }
            }
        });
    }

    private void startRecording() {

        try {
            binding.recordTimer.setBase(SystemClock.elapsedRealtime());
            binding.recordTimer.start();
            String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd__hh__mm__ss", Locale.getDefault());
            Date date = new Date();

            recordFile = "Recording_" + dateFormat.format(date) + ".3gp";
            binding.recordFilename.setText(("Recoding, File Name: " + recordFile));
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e);
        }


        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void stopRecording() {
        binding.recordTimer.stop();
        binding.recordFilename.setText(("Recoding Stopped, File Saved: " + recordFile));
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}