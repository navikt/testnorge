import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatStringDates } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import * as _ from 'lodash-es'
import React from 'react'
import { Fartoy } from '@/components/fagsystem/aareg/visning/partials/Fartoy'

export const Arbeidsavtaler = ({ data }) => {
	if (!data || data.length === 0) {
		return null
	}

	const detaljer = data[0]

	if (_.isEmpty(detaljer)) {
		return null
	}

	return (
		<React.Fragment>
			<h4>Ansettelsesdetaljer</h4>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Yrke" value={detaljer.yrke} kodeverk={ArbeidKodeverk.Yrker} />
					<TitleValue
						title="Ansettelsesform"
						value={detaljer.ansettelsesform}
						kodeverk={ArbeidKodeverk.AnsettelsesformAareg}
					/>
					<TitleValue
						title="Stillingsprosent"
						value={detaljer.stillingsprosent === 0 ? '0' : detaljer.stillingsprosent}
					/>
					<TitleValue
						title="Endringsdato stillingsprosent"
						value={formatStringDates(detaljer.sistStillingsendring)}
					/>
					<TitleValue
						title="Endringsdato lønn"
						value={formatStringDates(
							detaljer.sisteLoennsendringsdato || detaljer.sistLoennsendring,
						)}
					/>
					<TitleValue
						title="Arbeidstidsordning"
						value={detaljer.arbeidstidsordning}
						kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
					/>
					<TitleValue
						title="Avtalte arbeidstimer per uke"
						value={detaljer.beregnetAntallTimerPrUke || detaljer.antallTimerPrUke}
					/>
				</div>
			</ErrorBoundary>
			{detaljer.type === 'Maritim' && <Fartoy data={detaljer} />}
		</React.Fragment>
	)
}
