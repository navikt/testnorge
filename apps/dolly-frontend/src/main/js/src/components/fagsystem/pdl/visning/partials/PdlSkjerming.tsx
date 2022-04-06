import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { addDays, isBefore } from 'date-fns'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

type PdlSkjermingProps = {
	data: Skjerming
	loading?: boolean
}

type Skjerming = {
	egenAnsattDatoTom?: string
	egenAnsattDatoFom?: string
}

export const PdlSkjerming = ({ data, loading = false }: PdlSkjermingProps) => {
	if (loading) return <Loading />
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
