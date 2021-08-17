import React, { Fragment } from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Personnavn } from './Personnavn'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const KontaktinformasjonForDoedsbo = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon for dødsbo" iconKind="doedsbo" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div className="person-visning_content" key={idx}>
							{id.kontaktpersonUtenIdNummerSomAdressat && (
								<Fragment>
									<Personnavn data={id.kontaktpersonUtenIdNummerSomAdressat.navn} />

									<TitleValue
										title="Fødselsdato"
										value={Formatters.formatStringDates(
											id.kontaktpersonUtenIdNummerSomAdressat.foedselsdato
										)}
									/>
								</Fragment>
							)}

							{id.personSomKontakt && (
								<Fragment>
									<TitleValue
										title="FNR/DNR/BOST"
										value={id.personSomKontakt.identifikasjonsnummer}
									/>
									<TitleValue
										title="Fødselsdato"
										value={Formatters.formatStringDates(id.personSomKontakt.foedselsdato)}
									/>
									<Personnavn data={id.personSomKontakt.personnavn} />
								</Fragment>
							)}

							{id.advokatSomKontakt && (
								<Fragment>
									<TitleValue
										title="Organisasjonsnavn"
										value={id.advokatSomKontakt.organisasjonsnavn}
									/>
									<TitleValue
										title="Organisasjonsnummer"
										value={id.advokatSomKontakt.organisasjonsnummer}
									/>
									<Personnavn data={id.advokatSomKontakt.personnavn} />
								</Fragment>
							)}

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

									<Personnavn data={id.organisasjonSomKontakt.personnavn} />
								</Fragment>
							)}

							{id.adresse && (
								<Fragment>
									<TitleValue title="Adresselinje 1" value={id.adresse.adresselinje1} />
									<TitleValue title="Adresselinje 2" value={id.adresse.adresselinje2} />
									<TitleValue
										title="Postnummer og -sted"
										value={id.adresse.postnummer + ' ' + id.adresse.poststedsnavn}
									/>
									<TitleValue
										title="Land"
										value={id.adresse.landkode}
										kodeverk={AdresseKodeverk.PostadresseLand}
									/>
								</Fragment>
							)}
							<TitleValue title="Skifteform" value={id.skifteform} />
							<TitleValue
								title="Dato Utstedt"
								value={Formatters.formatStringDates(id.attestutstedelsesdato)}
							/>
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
