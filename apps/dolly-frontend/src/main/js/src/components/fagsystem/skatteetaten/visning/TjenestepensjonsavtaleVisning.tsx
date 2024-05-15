import Panel from '@/components/ui/panel/Panel'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'

export const Tjenestepensjonsavtale = ({ tjenestepensjonsavtale }: any) => {
	return (
		<>
			<TitleValue title="Inntektsmottaker" value={tjenestepensjonsavtale?.inntektsmottaker} />
			<TitleValue
				title="Pensjonsinnretning"
				value={tjenestepensjonsavtale?.pensjonsinnretningOrgnr}
			/>
			<TitleValue
				title="Opplysningspliktig"
				value={tjenestepensjonsavtale?.opplysningspliktigOrgnr}
			/>
			<TitleValue title="Periode" value={tjenestepensjonsavtale?.periode} />
		</>
	)
}

export const TjenestepensjonsavtaleVisning = ({ tpListe }: any) => {
	if (!tpListe || tpListe.length < 1) {
		return null
	}

	return (
		<Panel heading="Tjenestepensjonsavtale">
			<div className="person-visning_content">
				<DollyFieldArray data={tpListe} header={null} nested>
					{(tjenestepensjonsavtale: any) => {
						return <Tjenestepensjonsavtale tjenestepensjonsavtale={tjenestepensjonsavtale} />
					}}
				</DollyFieldArray>
			</div>
		</Panel>
	)
}
