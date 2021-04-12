package com.example.myaudiorecoder.Ui.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.myaudiorecoder.Adatpter.AudioListAdapter;
import com.example.myaudiorecoder.R;
import com.example.myaudiorecoder.databinding.FragmentAudioListBinding;
import com.example.myaudiorecoder.databinding.PlayerSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.onItemClick {

    private FragmentAudioListBinding binding;
    private PlayerSheetBinding includeLayoutBinding;
    private BottomSheetBehavior bottomSheetBehavior;
    private File[] allFiles;
    private AudioListAdapter adapter;
    private String TAG = "AudioListFragment";
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private File fileToPlay;
    private Handler seekBarHandler;
    private Runnable updateSeekbar;

    // TODO: Rename parameter arguments, choose names that match


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from(includeLayoutBinding.getRoot());
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        adapter = new AudioListAdapter(allFiles, this);
        binding.audioListView.setHasFixedSize(true);
        binding.audioListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        includeLayoutBinding.playerPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseAudio();
                }else {
                    if(fileToPlay!=null){
                        resumeAudio();
                    }

                }
            }
        });
        includeLayoutBinding.playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay!=null){
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }

            }
        });


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAudioListBinding.inflate(inflater, container, false);
        includeLayoutBinding = binding.playerSheet;
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        includeLayoutBinding = null;
    }

    @Override
    public void onClickListner(File file, int position) {
        if (isPlaying) {
            stopAudio();
            playAudio(fileToPlay);
        } else {
            fileToPlay = file;
            playAudio(fileToPlay);
        }
        Log.d(TAG, "getName: " + file.getName());
    }

    private void stopAudio() {
        isPlaying = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.stop();
        if (getContext() != null)
            includeLayoutBinding.playerPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.player_play_btn, getContext().getTheme()));
        includeLayoutBinding.playerHeaderTitle.setText("Stopped");
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        if (getContext() != null)
            includeLayoutBinding.playerPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.player_play_btn, getContext().getTheme()));
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        if (getContext() != null)
            includeLayoutBinding.playerPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.player_pause_btn, getContext().getTheme()));
        isPlaying = true;
        updateRunnable();
        seekBarHandler.postDelayed(updateSeekbar, 0);

    }

    private void playAudio(File playAudio) {

        isPlaying = true;
        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (getContext() != null)
            includeLayoutBinding.playerPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.player_pause_btn, getContext().getTheme()));
        includeLayoutBinding.playerFilename.setText(fileToPlay.getName());
        includeLayoutBinding.playerHeaderTitle.setText("Playing");
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                includeLayoutBinding.playerHeaderTitle.setText("Finished");
            }
        });
        includeLayoutBinding.playerSeekbar.setMax(mediaPlayer.getDuration());
        seekBarHandler = new Handler();
        updateRunnable();
        seekBarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                includeLayoutBinding.playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }
}