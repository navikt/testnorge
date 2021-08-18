import React from 'react'
import { Adresse } from '~/components/fagsystem/organisasjoner/types'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

type Props = {
	adresse: Adresse
}

export default ({ adresse }: Props) => (
	<>
		<TitleValue
			title={adresse.adressetype === 'FADR' ? 'Forretningsadresse' : 'Postadresse'}
			size="medium"
		>
			<div>{adresse.adresselinjer[0]}</div>
			<div>{adresse.adresselinjer[1]}</div>
			<div>{adresse.adresselinjer[2]}</div>
			<div>
				<KodeverkConnector navn="Postnummer" value={adresse.postnr}>
					{(v: any, verdi: any) => <span>{verdi ? verdi.label : adresse.postnummer}</span>}
				</KodeverkConnector>
			</div>
			<div>
				<KodeverkConnector navn="LandkoderISO2" value={adresse.landkode}>
					{(v: any, verdi: any) => <span>{verdi ? verdi.label : adresse.landkode}</span>}
				</KodeverkConnector>
			</div>
		</TitleValue>
	</>
)
