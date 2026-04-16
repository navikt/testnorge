import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, codeToNorskLabel } from '@/utils/DataFormatter'
import React from 'react'
import Panel from '@/components/ui/panel/Panel'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

export const SummertSkattegrunnlag = ({ summertSkattegrunnlag }: any) => {
	return (
		<>
			<TitleValue title="Inntektsår" value={summertSkattegrunnlag?.inntektsaar} />
			<TitleValue title="Stadie" value={codeToNorskLabel(summertSkattegrunnlag?.stadie)} />
			<TitleValue
				title="Type oppgjør"
				value={codeToNorskLabel(summertSkattegrunnlag?.typeOppgjoer)}
			/>
			<TitleValue
				title="Alminnelig inntekt før særfradrag"
				value={summertSkattegrunnlag?.alminneligInntektFoerSaerfradragBeloep}
			/>
			<div className={'fullWidth'}>
				<TitleValue
					title="Teknisk navn"
					value={arrayToString(
						summertSkattegrunnlag?.tekniskNavn?.map((navn) => codeToNorskLabel(navn)),
					)}
					size="xlarge"
				/>
			</div>
		</>
	)
}

export const SummertSkattegrunnlagVisning = ({ summertSkattegrunnlagListe }: any) => {
	if (!summertSkattegrunnlagListe || summertSkattegrunnlagListe.length < 1) {
		return null
	}

	return (
		<Panel heading="Summert skattegrunnlag">
			<div className="person-visning_content">
				<DollyFieldArray data={summertSkattegrunnlagListe} nested>
					{(summertSkattegrunnlag: any) => {
						return <SummertSkattegrunnlag summertSkattegrunnlag={summertSkattegrunnlag} />
					}}
				</DollyFieldArray>
			</div>
		</Panel>
	)
}
