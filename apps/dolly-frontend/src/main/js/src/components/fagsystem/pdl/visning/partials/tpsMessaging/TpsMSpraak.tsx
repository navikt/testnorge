import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

type TpsMSpraakProps = {
	data: Spraak
}

type Spraak = {
	sprakKode: string
}

export const TpsMSpraak = ({ data }: TpsMSpraakProps) => {
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
