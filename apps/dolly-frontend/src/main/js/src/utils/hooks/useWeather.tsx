import useSWR from 'swr'
import { fetcher } from '@/api'

const fyrstikkAlleenForecastUrl =
	'https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=59.91254828924253&lon=10.796522002335804'

export enum NEDBOER_TYPE {
	SNOW,
	RAIN,
}

export const useWeatherFyrstikkAlleen = () => {
	const { data, isLoading, error } = useSWR<any, Error>(fyrstikkAlleenForecastUrl, fetcher, {
		dedupingInterval: 60000,
	})

	const millimeterNedboer =
		data?.properties?.timeseries?.[0]?.data?.next_6_hours?.details?.precipitation_amount

	const nedBoerType = data?.properties?.timeseries?.[0]?.data?.next_6_hou

	return {
		millimeterNedboer: millimeterNedboer ? millimeterNedboer * 10 : 0,
		nedBoerType: nedBoerType?.includes('snow') ? NEDBOER_TYPE.SNOW : NEDBOER_TYPE.RAIN,
		loading: isLoading,
		error: error,
	}
}
