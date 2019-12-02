import React from 'react'
import Loading from '~/components/ui/loading/Loading'

export const Arbeidsavtaler = ({ data, loading }) => {
	if (loading) return <Loading label="laster Aareg-data" />
	if (!data) return false

	return (
		<div>
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div>
						{/* arbeidsavtale:
						 antallKonverterteTimer
						 arbeidstidsordning (kodeverk)
						 avloeningstype (kodeverk)
						 avtaltArbeidstimerPerUke
						 endringsdatoStillingsprosent
						 sisteLoennsendringsdato
						 stillingsprosent
						 yrke */}
					</div>
				))}
			</div>
		</div>
	)
}
