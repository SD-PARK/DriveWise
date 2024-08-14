library(MASS)
setwd("C:/Users/bit/git/DriveWise/02-Data/02-R")
load("travel_time_model.RData")

predictTravelTime <- function(hour, average_speed, lanes, max_speed_limit, length, tci) {
  hour <- sprintf("%02d", hour)
  
  input_data <- data.frame(
    hour = as.factor(hour),
    average_speed = average_speed,
    lanes = lanes,
    max_speed_limit = max_speed_limit,
    length = length,
    tci = tci
  )
  
  predicted_travel_time <- predict(model.2, newdata=input_data)
  return(predicted_travel_time)
}

testFnc <- function(hour, average_speed, lanes, max_speed_limit, length, tci) {
  time <- predictTravelTime(10, 10.000000, 2, 60, 33.900000, 30.000000)
  return(time)
}

testFnc(10, 10.000000, 2, 60, 33.900000, 30.000000)
