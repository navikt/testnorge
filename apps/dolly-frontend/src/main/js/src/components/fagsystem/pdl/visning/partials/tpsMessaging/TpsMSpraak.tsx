import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { addDays, isBefore } from 'date-fns'
import Formatters from '~/utils/DataFormatter'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

type TpsMSpraakProps = {
	data: Spraak
	loading?: boolean
}

type Spraak = {
	sprakKode: string
}

export const TpsMSpraak = ({ data, loading = false }: TpsMSpraakProps) => {
	if (!data?.sprakKode) return null
	return (
		<>
			<TitleValue
				title="Språk"
				kodeverk={PersoninformasjonKodeverk.Spraak}
				value={data.sprakKode}
			/>
		</>
	)
}
