import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { OrganisasjonKodeverk } from '~/config/kodeverk'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import '~/pages/gruppe/PersonVisning/PersonVisning.less'

export const Detaljer = ({ data }) => {
	return (
		<div className="person-visning">
			<SubOverskrift label="Detaljer" iconKind="personinformasjon" />
			<div className="person-visning_content">
				<TitleValue title="Orgnr." value={data[0].organisasjonsnummer} />
				<TitleValue title="Navn" value={data[0].organisasjonsnavn} />
				<TitleValue title="Orgforvalter-ID" value={data[0].id} />
				<TitleValue title="Enhetstype" value={data[0].enhetstype} />
				<TitleValue
					title="Næringskode"
					kodeverk={OrganisasjonKodeverk.Næringskoder}
					value={data[0].naeringskode}
				/>
				<TitleValue title="Formål" value={data[0].formaal} />
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
											{(v, verdi) => <span>{verdi ? verdi.label : adresse.postnummer}</span>}
										</KodeverkConnector>
									</div>
									<div>
										<KodeverkConnector navn="LandkoderISO2" value={adresse.landkode}>
											{(v, verdi) => <span>{verdi ? verdi.label : adresse.landkode}</span>}
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
