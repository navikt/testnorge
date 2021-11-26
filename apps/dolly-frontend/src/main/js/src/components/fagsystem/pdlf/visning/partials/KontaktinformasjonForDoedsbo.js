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
						const {
							skifteform,
							attestutstedelsesdato,
							adresse,
							advokatSomKontakt,
							organisasjonSomKontakt,
							personSomKontakt,
						} = item

						const getKontakttype = () => {
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
								<TitleValue title="Skifteform" value={skifteform} />
								<TitleValue
									title="Utstedelsesdato skifteattest"
									value={Formatters.formatDate(attestutstedelsesdato)}
								/>
								<TitleValue
									title="Land"
									value={adresse?.landkode}
									kodeverk={AdresseKodeverk.PostadresseLand}
								/>
								<TitleValue title="Adresselinje 1" value={adresse?.adresselinje1} />
								<TitleValue title="Adresselinje 2" value={adresse?.adresselinje2} />
								<TitleValue
									title="Postnummer og -sted"
									value={adresse?.postnummer + ' ' + adresse?.poststedsnavn}
								/>
								<TitleValue title="Kontakttype" value={getKontakttype()} />
								<TitleValue
									title="Organisasjonsnummer"
									value={
										advokatSomKontakt?.organisasjonsnummer ||
										organisasjonSomKontakt?.organisasjonsnummer
									}
								/>
								<TitleValue
									title="Organisasjonsnavn"
									value={
										advokatSomKontakt?.organisasjonsnavn ||
										organisasjonSomKontakt?.organisasjonsnavn
									}
								/>
								<TitleValue
									title="Identifikasjonsnummer"
									value={personSomKontakt?.identifikasjonsnummer}
								/>
								<TitleValue
									title="Fødselsdato"
									value={Formatters.formatDate(personSomKontakt?.foedselsdato)}
								/>
								<Personnavn
									data={
										advokatSomKontakt?.kontaktperson ||
										organisasjonSomKontakt?.kontaktperson ||
										personSomKontakt?.navn ||
										personSomKontakt?.personnavn
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
