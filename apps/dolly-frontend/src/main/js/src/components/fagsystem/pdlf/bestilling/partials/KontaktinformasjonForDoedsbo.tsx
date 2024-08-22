import { KontaktinformasjonForDoedsboData, Navn } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showKodeverkLabel, showLabel } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'
import _get from 'lodash/get'

type KontaktinformasjonForDoedsboTypes = {
	kontaktinformasjonForDoedsboListe: Array<KontaktinformasjonForDoedsboData>
}

export const KontaktinformasjonForDoedsbo = ({
	kontaktinformasjonForDoedsboListe,
}: KontaktinformasjonForDoedsboTypes) => {
	if (!kontaktinformasjonForDoedsboListe || kontaktinformasjonForDoedsboListe.length < 1) {
		return null
	}

	const getKontaktperson = (kontaktperson: Navn) => {
		return (
			<>
				<TitleValue title="Kontaktperson fornavn" value={kontaktperson?.fornavn} />
				<TitleValue title="Kontaktperson mellomnavn" value={kontaktperson?.mellomnavn} />
				<TitleValue title="Kontaktperson etternavn" value={kontaktperson?.etternavn} />
			</>
		)
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
											value={advokatSomKontakt.organisasjonsnummer}
										/>
										<TitleValue
											title="Organisasjonsnavn"
											value={advokatSomKontakt.organisasjonsnavn}
										/>
										{getKontaktperson(advokatSomKontakt.kontaktperson)}
									</>
								)}
								{organisasjonSomKontakt && (
									<>
										<TitleValue
											title="Organisasjonsnummer"
											value={organisasjonSomKontakt.organisasjonsnummer}
										/>
										<TitleValue
											title="Organisasjonsnavn"
											value={organisasjonSomKontakt.organisasjonsnavn}
										/>
										{getKontaktperson(organisasjonSomKontakt.kontaktperson)}
									</>
								)}
								{personSomKontakt && (
									<>
										<TitleValue
											title="Identifikasjonsnummer"
											value={personSomKontakt.identifikasjonsnummer}
										/>
										<TitleValue
											title="Fødselsdato"
											value={formatDate(personSomKontakt.foedsalsdato)}
										/>
										{getKontaktperson(personSomKontakt.navn)}
									</>
								)}
								<TitleValue
									title="Land"
									value={showKodeverkLabel(AdresseKodeverk.PostadresseLand, adresse?.landkode)}
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
								{personSomKontakt?.nyKontaktperson && (
									<EkspanderbarVisning
										vis={_get(personSomKontakt, 'nyKontaktperson')}
										header="NY KONTAKTPERSON"
									>
										<RelatertPerson personData={personSomKontakt.nyKontaktperson} />
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
