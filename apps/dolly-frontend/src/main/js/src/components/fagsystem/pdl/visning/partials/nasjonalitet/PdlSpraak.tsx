import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { addDays, isBefore } from 'date-fns'
import Formatters from '~/utils/DataFormatter'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

type PdlSpraakProps = {
	data: Spraak
	loading?: boolean
}

type Spraak = {
	sprakKode: string
}

export const PdlSpraak = ({ data, loading = false }: PdlSpraakProps) => {
	if (loading) return <Loading />
	if (!data?.sprakKode) return null
	return (
		<>
			<h3>Språk</h3>
			<TitleValue kodeverk={PersoninformasjonKodeverk.Spraak} value={data.sprakKode} />
		</>
	)
}
