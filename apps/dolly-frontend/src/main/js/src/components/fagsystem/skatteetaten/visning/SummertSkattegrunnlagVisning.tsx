import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, codeToNorskLabel } from '@/utils/DataFormatter'
import React from 'react'

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
						summertSkattegrunnlag?.tekniskNavn?.map((navn: string) => codeToNorskLabel(navn)),
					)}
					size="xlarge"
				/>
			</div>
		</>
	)
}
