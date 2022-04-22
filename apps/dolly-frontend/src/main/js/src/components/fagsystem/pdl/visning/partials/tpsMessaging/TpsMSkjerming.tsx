import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { addDays, isBefore } from 'date-fns'
import Formatters from '~/utils/DataFormatter'

type TpsMSkjermingProps = {
	data: Skjerming
}

type Skjerming = {
	egenAnsattDatoTom?: string
	egenAnsattDatoFom?: string
}

export const TpsMSkjerming = ({ data }: TpsMSkjermingProps) => {
	if (!data?.egenAnsattDatoFom) return null
	return (
		<>
			<TitleValue
				title="Har skjerming"
				value={
					data.egenAnsattDatoTom &&
					isBefore(new Date(data.egenAnsattDatoTom), addDays(new Date(), -1))
						? 'NEI'
						: 'JA'
				}
			/>
			<TitleValue title="Skjerming fra" value={Formatters.formatDate(data.egenAnsattDatoFom)} />
			{data?.egenAnsattDatoTom && (
				<TitleValue title="Skjerming til" value={Formatters.formatDate(data.egenAnsattDatoTom)} />
			)}
		</>
	)
}
