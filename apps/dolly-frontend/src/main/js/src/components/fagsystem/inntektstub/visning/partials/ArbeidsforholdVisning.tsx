import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel, formatStringDates } from '@/utils/DataFormatter'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'

export const ArbeidsforholdVisning = ({ data }) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<React.Fragment>
			<h4>Arbeidsforhold (Aareg)</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue
								title="Arbeidsforholdstype"
								value={codeToNorskLabel(id.arbeidsforholdstype)}
							/>
							<TitleValue title="Startdato" value={formatStringDates(id.startdato)} />
							<TitleValue title="Sluttdato" value={formatStringDates(id.sluttdato)} />
							<TitleValue
								title="Timer per uke (full stilling)"
								value={id.antallTimerPerUkeSomEnFullStillingTilsvarer}
							/>
							<TitleValue
								title="Avlønningstype"
								value={id.avloenningstype}
								kodeverk={ArbeidKodeverk.Avloenningstyper}
							/>
							<TitleValue title="Yrke" value={id.yrke} kodeverk={ArbeidKodeverk.Yrker} />
							<TitleValue
								title="Arbeidstidsordning"
								value={id.arbeidstidsordning}
								kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
							/>
							<TitleValue title="Stillingsprosent" value={id.stillingsprosent} />
							<TitleValue
								title="Siste lønnsendringsdato"
								value={formatStringDates(id.sisteLoennsendringsdato)}
							/>
							<TitleValue
								title="Stillingsprosentendring"
								value={formatStringDates(id.sisteDatoForStillingsprosentendring)}
							/>
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
