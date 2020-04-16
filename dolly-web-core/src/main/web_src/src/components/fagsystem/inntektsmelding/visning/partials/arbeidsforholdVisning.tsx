import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { InntektstubKodeverk } from '~/config/kodeverk'

interface ArbeidsforholdVisning {
	data?: any
}
export default ({ data }: ArbeidsforholdVisning) => {
	if (!data) return null

	return (
		<>
			<h4>Arbeidsforhold</h4>
			<div className="person-visning_content">
				<TitleValue title="Beløp" value={data.beregnetInntekt.beloep} />
				<TitleValue
					title="Første fraværsdag"
					value={Formatters.formatDate(data.beregnetInntekt.foersteFravaersdag)}
				/>
				<TitleValue
					title="Årsak ved endring"
					value={Formatters.codeToNorskLabel(data.beregnetInntekt.aarsakVedEndring)}
				/>
				{data.avtaltFerieListe && (
					<DollyFieldArray data={data.avtaltFerieListe} header="Avtalt ferie" nested>
						{(id: any, idx: number) => (
							<div className="person-visning_content" key={idx}>
								<TitleValue title="Beløp" value={id.beloep} />
								<TitleValue
									title="Beskrivelse"
									value={id.beskrivelse}
									kodeverk={InntektstubKodeverk.Fradragbeskrivelse}
								/>
							</div>
						)}
					</DollyFieldArray>
				)}
			</div>
		</>
	)
}
