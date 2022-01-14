import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

type Data = {
	data: Array<AdressebeskyttelseData>
}

type AdressebeskyttelseData = {
	gradering: string
}

export const Adressebeskyttelse = ({ data }: Data) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<SubOverskrift label="Adressebeskyttelse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adressebeskyttelse: AdressebeskyttelseData, idx: number) => {
							return (
								<>
									<div className="person-visning_content" key={idx}>
										<TitleValue
											title="Gradering"
											value={Formatters.showLabel('gradering', adressebeskyttelse.gradering)}
										/>
									</div>
								</>
							)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
