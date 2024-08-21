package com.drivewise.smarttraffic.predictor;

import com.drivewise.smarttraffic.dto.PredictInputDTO;

public interface IIndicatorPredictor<T extends Number> {
	T predict(PredictInputDTO dto);
}
