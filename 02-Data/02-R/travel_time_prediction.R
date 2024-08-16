library(MASS)
setwd("C:/Users/bit/git/DriveWise/02-Data/02-R")
load("travel_time_model.RData")

predictTravelTime <- function(hour, average_speed, lanes, max_speed_limit, length, tci) {
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