import { KontaktinformasjonForDoedsboData, Navn } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'
import _get from 'lodash/get'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

type KontaktinformasjonForDoedsboTypes = {
	kontaktinformasjonForDoedsboListe: Array<KontaktinformasjonForDoedsboData>
}

export const KontaktinformasjonForDoedsbo = ({
	kontaktinformasjonForDoedsboListe,
}: KontaktinformasjonForDoedsboTypes) => {
	if (!kontaktinformasjonForDoedsboListe || kontaktinformasjonForDoedsboListe.length < 1) {
		return null
	}

	const getKontaktperson = (kontaktperson: Navn | undefined) => {
		if (!kontaktperson?.fornavn && !kontaktperson?.mellomnavn && !kontaktperson?.etternavn) {
			return null
		}
		return `${kontaktperson?.fornavn} ${kontaktperson?.mellomnavn} ${kontaktperson?.etternavn}`
	}

	const getOrganisasjon = (organisasjon: any) => {
		if (!organisasjon.organisasjonsnummer && !organisasjon.organisasjonsnavn) {
			return null
		}
		return `${organisasjon.organisasjonsnummer} - ${organisasjon.organisasjonsnavn}`
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Kontaktinformasjon for dødsbo</BestillingTitle>
				<DollyFieldArray
					header="Kontaktinformasjon for dødsbo"
					data={kontaktinformasjonForDoedsboListe}
				>
					{(kontaktinformasjonForDoedsbo: KontaktinformasjonForDoedsboData, idx: number) => {
						const { advokatSomKontakt, organisasjonSomKontakt, personSomKontakt, adresse } =
							kontaktinformasjonForDoedsbo

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
							<React.Fragment key={idx}>
								<TitleValue title="Skifteform" value={kontaktinformasjonForDoedsbo.skifteform} />
								<TitleValue
									title="Utstedelsesdato skifteattest"
									value={formatDate(kontaktinformasjonForDoedsbo.attestutstedelsesdato)}
								/>
								<div className="flexbox--full-width" style={{ margin: '15px 0 -15px 0' }}>
									<BestillingTitle>Kontakt</BestillingTitle>
									<BestillingData>
										<TitleValue
											title="Kontakttype"
											value={
												kontaktinformasjonForDoedsbo.kontaktType
													? showLabel('kontaktType', kontaktinformasjonForDoedsbo.kontaktType)
													: getKontakttype()
											}
										/>
										{advokatSomKontakt && (
											<>
												<TitleValue
													title="Organisasjonsnummer"
													value={getOrganisasjon(advokatSomKontakt)}
												/>
												<TitleValue
													title="Kontaktperson"
													value={getKontaktperson(advokatSomKontakt.kontaktperson)}
												/>
											</>
										)}
										{organisasjonSomKontakt && (
											<>
												<TitleValue
													title="Organisasjonsnummer"
													value={getOrganisasjon(organisasjonSomKontakt)}
												/>
												<TitleValue
													title="Kontaktperson"
													value={getKontaktperson(organisasjonSomKontakt.kontaktperson)}
												/>
											</>
										)}
										{personSomKontakt && (
											<>
												<TitleValue
													title="Kontaktperson"
													value={personSomKontakt.identifikasjonsnummer}
												/>
												<TitleValue
													title="Fødselsdato"
													value={formatDate(personSomKontakt.foedselsdato)}
												/>
												<TitleValue
													title="Kontaktperson"
													value={getKontaktperson(personSomKontakt.navn)}
												/>
											</>
										)}
									</BestillingData>
								</div>
								{personSomKontakt?.nyKontaktperson && (
									<EkspanderbarVisning
										vis={_get(personSomKontakt, 'nyKontaktperson')}
										header="NY KONTAKTPERSON"
									>
										<RelatertPerson personData={personSomKontakt.nyKontaktperson} />
									</EkspanderbarVisning>
								)}
								{adresse && !isEmpty(adresse) && (
									<EkspanderbarVisning vis={adresse} header="ADRESSE">
										<TitleValue
											title="Land"
											value={adresse?.landkode}
											kodeverk={AdresseKodeverk.PostadresseLand}
										/>
										<TitleValue title="Adresselinje 1" value={adresse?.adresselinje1} />
										<TitleValue title="Adresselinje 2" value={adresse?.adresselinje2} />
										<TitleValue
											title="Postnummer og -sted"
											value={
												(adresse?.postnummer || adresse?.poststedsnavn) &&
												`${adresse?.postnummer} ${adresse?.poststedsnavn}`
											}
										/>
									</EkspanderbarVisning>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
