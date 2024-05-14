import Panel from '@/components/ui/panel/Panel'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { arrayToString, codeToNorskLabel, oversettBoolean } from '@/utils/DataFormatter'

export const InntektVisning = ({ inntektListe }) => {
	if (!inntektListe || inntektListe.length < 1) {
		return null
	}

	return (
		<Panel heading="Inntekt A-ordningen">
			<div className="person-visning_content">
				<DollyFieldArray data={inntektListe} header={null} nested>
					{(inntekt: any) => {
						return (
							<>
								<TitleValue title="Periode" value={inntekt.periode} />
								<TitleValue title="Opplysningspliktig" value={inntekt.opplysningspliktig} />
								<TitleValue
									title="Inntektstype"
									value={arrayToString(
										inntekt.inntektstype?.map((type: string) => codeToNorskLabel(type)),
									)}
								/>
								<TitleValue
									title="Beskrivelse"
									value={arrayToString(
										inntekt.beskrivelse?.map((beskr: string) => codeToNorskLabel(beskr)),
									)}
								/>
								<TitleValue
									title="Forskuddstrekk"
									value={arrayToString(
										inntekt.forskuddstrekk?.map((trekk: string) => codeToNorskLabel(trekk)),
									)}
								/>
								<TitleValue title="Har historikk" value={oversettBoolean(inntekt.harHistorikk)} />
							</>
						)
					}}
				</DollyFieldArray>
			</div>
		</Panel>
	)
}
