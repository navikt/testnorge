import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import {
	Arbeidsforhold,
	AvtaltFerie
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface ArbeidsforholdVisning {
	data?: Arbeidsforhold
}

export default ({ data }: ArbeidsforholdVisning) => {
	if (!data) return null

	return (
		<>
			<h4>Arbeidsforhold</h4>
			<div className="person-visning_content">
				<TitleValue title="Arbeidsforhold-ID" value={data.arbeidsforholdId} />
				<TitleValue title="Beløp" value={data.beregnetInntekt.beloep} />
				<TitleValue
					title="Årsak ved endring"
					value={Formatters.codeToNorskLabel(data.beregnetInntekt.aarsakVedEndring)}
				/>
				<TitleValue
					title="Første fraværsdag"
					value={Formatters.formatDate(data.foersteFravaersdag)}
				/>
				{data.avtaltFerieListe && (
					<ErrorBoundary>
						<DollyFieldArray data={data.avtaltFerieListe} header="Avtalt ferie" nested>
							{(id: AvtaltFerie, idx: number) => (
								<div className="person-visning_content" key={idx}>
									<TitleValue title="Fra og med" value={Formatters.formatDate(id.fom)} />
									<TitleValue title="Til og med" value={Formatters.formatDate(id.tom)} />
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				)}
			</div>
		</>
	)
}
