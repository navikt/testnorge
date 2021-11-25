import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Personnavn } from './Personnavn'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const KontaktinformasjonForDoedsbo = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon for dødsbo" iconKind="doedsbo" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(item, idx) => {
						const getKontaktype = () => {
							const { advokatSomKontakt, personSomKontakt, organisasjonSomKontakt } = item
							if (advokatSomKontakt) {
								return 'Advokat'
							} else if (personSomKontakt) {
								return 'Person'
							} else if (organisasjonSomKontakt) {
								return 'Organisasjon'
							} else return null
						}

						return (
							<div className="person-visning_content" key={idx}>
								<TitleValue title="Skifteform" value={item.skifteform} />
								<TitleValue
									title="Utstedelsesdato skifteattest"
									value={Formatters.formatDate(item.attestutstedelsesdato)}
								/>
								<TitleValue
									title="Land"
									value={item.adresse?.landkode}
									kodeverk={AdresseKodeverk.PostadresseLand}
								/>
								<TitleValue title="Adresselinje 1" value={item.adresse?.adresselinje1} />
								<TitleValue title="Adresselinje 2" value={item.adresse?.adresselinje2} />
								<TitleValue
									title="Postnummer og -sted"
									value={item.adresse?.postnummer + ' ' + item.adresse?.poststedsnavn}
								/>
								<TitleValue title="Kontakttype" value={getKontaktype()} />
								<TitleValue
									title="Organisasjonsnummer"
									value={
										item.advokatSomKontakt?.organisasjonsnummer ||
										item.organisasjonSomKontakt?.organisasjonsnummer
									}
								/>
								<TitleValue
									title="Organisasjonsnavn"
									value={
										item.advokatSomKontakt?.organisasjonsnavn ||
										item.organisasjonSomKontakt?.organisasjonsnavn
									}
								/>
								<TitleValue
									title="Identifikasjonsnummer"
									value={item.personSomKontakt?.identifikasjonsnummer}
								/>
								<TitleValue
									title="Fødselsdato"
									value={Formatters.formatDate(item.personSomKontakt?.foedselsdato)}
								/>
								<Personnavn
									data={
										item.advokatSomKontakt?.kontaktperson ||
										item.organisasjonSomKontakt?.kontaktperson ||
										item.personSomKontakt?.navn ||
										item.personSomKontakt?.personnavn
									}
								/>
							</div>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
