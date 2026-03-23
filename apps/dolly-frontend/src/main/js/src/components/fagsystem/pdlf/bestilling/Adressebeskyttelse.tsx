import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { AdressebeskyttelseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { showLabel } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type AdressebeskyttelseTypes = {
	adressebeskyttelseListe: Array<AdressebeskyttelseData>
}

export const Adressebeskyttelse = ({ adressebeskyttelseListe }: AdressebeskyttelseTypes) => {
	if (!adressebeskyttelseListe || adressebeskyttelseListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Adressebeskyttelse</BestillingTitle>
				<DollyFieldArray header="Adressebeskyttelse" data={adressebeskyttelseListe}>
					{(adressebeskyttelse: AdressebeskyttelseData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Gradering"
									value={showLabel('gradering', adressebeskyttelse.gradering)}
								/>
								<TitleValue title="Master" value={adressebeskyttelse.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
