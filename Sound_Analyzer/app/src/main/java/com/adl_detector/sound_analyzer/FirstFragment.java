package com.adl_detector.sound_analyzer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.adl_detector.sound_analyzer.databinding.FragmentFirstBinding;
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    public RecordingController recordingController;

    public interface RecordingController {
        void startRecording();
        void stopRecording();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            recordingController = (RecordingController) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement RecordingController");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonRecord.setOnClickListener(v -> {
            if (!MainActivity.isRecording) {
                ((MainActivity) requireActivity()).startRecording();
                binding.buttonRecord.setText("Stop Recording");
            } else {
                ((MainActivity) requireActivity()).stopRecording();
                binding.buttonRecord.setText("Start Recording");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        recordingController = null;
    }
}
