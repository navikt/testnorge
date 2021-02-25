import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { OrganisasjonKodeverk } from '~/config/kodeverk'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import '~/pages/gruppe/PersonVisning/PersonVisning.less'
import { EnhetData } from '../types'
import Formatters from '~/utils/DataFormatter'

type Detaljer = {
	data: Array<EnhetData>
}

export const Detaljer = ({ data }: Detaljer) => {
	return (
		<div className="person-visning">
			<SubOverskrift label="Detaljer" iconKind="personinformasjon" />
			<div className="person-visning_content">
				<TitleValue title="Orgnr." value={data[0].organisasjonsnummer} />
				<TitleValue title="Navn" value={data[0].organisasjonsnavn} />
				<TitleValue title="Enhetstype" value={data[0].enhetstype} />
				<TitleValue
					title="Næringskode"
					kodeverk={OrganisasjonKodeverk.Naeringskoder}
					value={data[0].naeringskode}
				/>
				<TitleValue
					title="Sektorkode"
					kodeverk={OrganisasjonKodeverk.Sektorkoder}
					value={data[0].sektorkode}
				/>
				<TitleValue title="Formål" value={data[0].formaal} />
				<TitleValue
					title="Stiftelsesdato"
					value={Formatters.formatStringDates(data[0].stiftelsesdato)}
				/>
				<TitleValue title="Målform" value={Formatters.showLabel('maalform', data[0].maalform)} />
			</div>

			{(data[0].telefon || data[0].epost || data[0].nettside) && (
				<>
					<h4>Kontaktdata</h4>
					<div className="person-visning_content">
						<TitleValue title="Telefon" value={data[0].telefon} />
						<TitleValue title="E-postadresse" value={data[0].epost} />
						<TitleValue title="Internettadresse" value={data[0].nettside} />
					</div>
				</>
			)}

			{data[0].adresser && (
				<>
					<h4>Adresser</h4>
					<div className="person-visning_content">
						{data[0].adresser.map((adresse, idx) => (
							<React.Fragment key={idx}>
								<TitleValue
									title={adresse.adressetype === 'FADR' ? 'Forretningsadresse' : 'Postadresse'}
									size="medium"
								>
									<div>{adresse.adresselinjer[0]}</div>
									<div>{adresse.adresselinjer[1]}</div>
									<div>{adresse.adresselinjer[2]}</div>
									<div>
										<KodeverkConnector navn="Postnummer" value={adresse.postnr}>
											{(v: any, verdi: any) => (
												<span>{verdi ? verdi.label : adresse.postnummer}</span>
											)}
										</KodeverkConnector>
									</div>
									<div>
										<KodeverkConnector navn="LandkoderISO2" value={adresse.landkode}>
											{(v: any, verdi: any) => (
												<span>{verdi ? verdi.label : adresse.landkode}</span>
											)}
										</KodeverkConnector>
									</div>
								</TitleValue>
							</React.Fragment>
						))}
					</div>
				</>
			)}
		</div>
	)
}
