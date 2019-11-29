import React, { Fragment } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { Personnavn } from './Personnavn'

export const DoedsboKontaktinfo = ({ data, loading }) => {
	if (loading) return <Loading label="laster PDL-data" />
	if (!data) return false

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon for dødsbo" />
			<div>
				{
					(console.log('data :', data),
					data.map((id, idx) => (
						<div className="person-visning_content" key={idx}>
							{/* Addressat eksistenssjekk */}
							{id.adressat && id.adressat.kontaktpersonUtenIdNummerSomAdressat && (
								<Fragment>
									{id.adressat.kontaktpersonUtenIdNummerSomAdressat.navn && (
										<Personnavn data={id.adressat.kontaktpersonUtenIdNummerSomAdressat.navn} />
									)}
									<TitleValue
										title="Fødselsdato"
										value={id.adressat.kontaktpersonUtenIdNummerSomAdressat.foedselsdato}
									/>
								</Fragment>
							)}

							{id.adressat && id.adressat.kontaktpersonMedIdNummerSomAdressat && (
								<TitleValue
									title="FNR/DNR/BOST"
									value={id.adressat.kontaktpersonMedIdNummerSomAdressat.idNummer}
								/>
							)}
							{id.adressat && id.adressat.advokatSomAdressat && (
								<Fragment>
									{/* Hvor har disse blitt av? */}
									<TitleValue
										title="Organisasjonsnavn"
										value={id.adressat.advokatSomAdressat.organisasjonsnavn}
									/>
									<TitleValue
										title="Organisasjonsnummer"
										value={id.adressat.advokatSomAdressat.organisasjonsnummer}
									/>
									{id.adressat.advokatSomAdressat.kontaktperson && (
										<Personnavn data={id.adressat.advokatSomAdressat.kontaktperson} />
									)}
								</Fragment>
							)}
							{/* Bestilling fungerer ikke imot organisasjon som kontakt */}
							{id.organisasjonSomKontakt && (
								<Fragment>
									<TitleValue
										title="Organisasjonsnavn"
										value={id.organisasjonSomKontakt.organisasjonsnavn}
									/>
									<TitleValue
										title="Organisasjonsnummer"
										value={id.organisasjonSomKontakt.organisasjonsnummer}
									/>
									{id.organisasjonSomKontakt.kontaktperson && (
										<Personnavn data={id.organisasjonSomKontakt.kontaktperson} />
									)}
								</Fragment>
							)}
							<TitleValue title="Adresselinje 1" value={id.adresselinje1} />
							<TitleValue title="Adresselinje 2" value={id.adresselinje2} />
							<TitleValue
								title="Postnummer og -sted"
								value={id.postnummer + ' ' + id.poststedsnavn}
							/>
							<TitleValue title="Landkode" value={id.landkode} />
							<TitleValue title="Skifteform" value={id.skifteform} />
							<TitleValue title="Dato Utstedt" value={id.utstedtDato} />
							<TitleValue title="Gyldig Fra" value={id.gyldigFom} />
						</div>
					)))
				}
			</div>
		</div>
	)
}
