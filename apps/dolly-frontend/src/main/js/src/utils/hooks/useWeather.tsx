import useSWR from 'swr'
import { fetcher } from '~/api'

const fyrstikkAlleenForecastUrl =
	'https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=59.91254828924253&lon=10.796522002335804'

export const useWeatherFyrstikkAlleen = () => {
	const { data, error } = useSWR<any, Error>(fyrstikkAlleenForecastUrl, fetcher)

	const beregnetMillimeterRegn =
		data?.properties?.timeseries?.[0]?.data?.next_6_hours?.details?.precipitation_amount

	return {
		beregnetMillimeterRegn: beregnetMillimeterRegn ? beregnetMillimeterRegn * 10 : 0,
		loading: !error && !data,
		error: error,
	}
}
