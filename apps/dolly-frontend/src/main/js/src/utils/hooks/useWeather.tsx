import useSWR from 'swr'
import { fetcher } from '~/api'

const getFyrstikkAlleenForecastUrl =
	'https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=59.91254828924253&lon=10.796522002335804'

export const useWeatherFyrstikkAlleen = () => {
	const { data, error } = useSWR<any, Error>(getFyrstikkAlleenForecastUrl, fetcher)

	const beregnetRegn =
		data?.properties?.timeseries?.[0]?.data?.next_1_hours?.details?.precipitation_amount

	return {
		beregnetRegn: beregnetRegn,
		loading: !error && !data,
		error: error,
	}
}
