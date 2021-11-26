import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ArbeidKodeverk } from '~/config/kodeverk'

type FartoyProps = {
	skipsregister: string
	skipstype: string
	fartsomraade: string
}

export const Fartoy = ({ data }: FartoyProps) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<h4>Fartøy</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Skipsregister"
					value={data.skipsregister}
					kodeverk={ArbeidKodeverk.Skipsregistre}
				/>
				<TitleValue
					title="Fartøystype"
					value={data.skipstype}
					kodeverk={ArbeidKodeverk.Skipstyper}
				/>
				<TitleValue
					title="Fartsområde"
					value={data.fartsomraade}
					kodeverk={ArbeidKodeverk.Fartsområder}
				/>
			</div>
		</>
	)
}
