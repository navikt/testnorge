import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel } from '@/utils/DataFormatter'
import Panel from '@/components/ui/panel/Panel'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'

export const Skattemelding = ({ skattemelding }: any) => {
	return (
		<>
			<TitleValue title="Inntektsår" value={skattemelding.inntektsaar} />
			<TitleValue
				title="Type skattemelding"
				value={codeToNorskLabel(skattemelding.skattemeldingstype)}
			/>
		</>
	)
}

export const SkattemeldingVisning = ({ skattemeldingListe }) => {
	if (!skattemeldingListe || skattemeldingListe.length < 1) {
		return null
	}

	return (
		<Panel heading="Skattemelding">
			<div className="person-visning_content">
				<DollyFieldArray data={skattemeldingListe} header={null} nested>
					{(skattemelding: any) => {
						return <Skattemelding skattemelding={skattemelding} />
					}}
				</DollyFieldArray>
			</div>
		</Panel>
	)
}
