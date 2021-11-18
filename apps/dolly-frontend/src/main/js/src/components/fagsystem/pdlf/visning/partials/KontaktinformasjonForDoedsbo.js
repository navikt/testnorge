import React, { Fragment } from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Personnavn } from './Personnavn'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import _has from 'lodash/has'

export const KontaktinformasjonForDoedsbo = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			<SubOverskrift label="Kontaktinformasjon for dødsbo" iconKind="doedsbo" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(item, idx) => {
						// todo: refactor
						const kontaktType = _has(item, 'advokatSomKontakt')
							? 'Advokat'
							: _has(item, 'personSomKontakt')
							? 'Person'
							: _has(item, 'organisasjonSomKontakt')
							? 'Organisasjon'
							: null

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
								<TitleValue title="Kontakttype" value={kontaktType} />
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
										item.personSomKontakt?.navn
									}
								/>

								{/*<Personnavn data={item.kontaktpersonUtenIdNummerSomAdressat.navn}/>*/}

								{/*<TitleValue*/}
								{/*	title="Fødselsdato"*/}
								{/*	value={Formatters.formatStringDates(*/}
								{/*		item.kontaktpersonUtenIdNummerSomAdressat.foedselsdato*/}
								{/*	)}*/}
								{/*/>*/}

								{/*{item.personSomKontakt && (*/}
								{/*	<Fragment>*/}
								{/*		<TitleValue*/}
								{/*			title="FNR/DNR/BOST"*/}
								{/*			value={item.personSomKontakt.identifikasjonsnummer}*/}
								{/*		/>*/}
								{/*		<TitleValue*/}
								{/*			title="Fødselsdato"*/}
								{/*			value={Formatters.formatStringDates(item.personSomKontakt.foedselsdato)}*/}
								{/*		/>*/}
								{/*		<Personnavn data={item.personSomKontakt.personnavn}/>*/}
								{/*	</Fragment>*/}
								{/*)}*/}

								{/*{item.advokatSomKontakt && (*/}
								{/*	<Fragment>*/}
								{/*		<TitleValue*/}
								{/*			title="Organisasjonsnavn"*/}
								{/*			value={item.advokatSomKontakt.organisasjonsnavn}*/}
								{/*		/>*/}
								{/*		<TitleValue*/}
								{/*			title="Organisasjonsnummer"*/}
								{/*			value={item.advokatSomKontakt.organisasjonsnummer}*/}
								{/*		/>*/}
								{/*		<Personnavn data={item.advokatSomKontakt.personnavn}/>*/}
								{/*	</Fragment>*/}
								{/*)}*/}

								{/*{item.organisasjonSomKontakt && (*/}
								{/*	<Fragment>*/}
								{/*		<TitleValue*/}
								{/*			title="Organisasjonsnavn"*/}
								{/*			value={item.organisasjonSomKontakt.organisasjonsnavn}*/}
								{/*		/>*/}
								{/*		<TitleValue*/}
								{/*			title="Organisasjonsnummer"*/}
								{/*			value={item.organisasjonSomKontakt.organisasjonsnummer}*/}
								{/*		/>*/}

								{/*		<Personnavn data={item.organisasjonSomKontakt.personnavn}/>*/}
								{/*	</Fragment>*/}
								{/*)}*/}

								{/*{item.adresse && (*/}
								{/*	<Fragment>*/}
								{/*		<TitleValue title="Adresselinje 1" value={item.adresse.adresselinje1} />*/}
								{/*		<TitleValue title="Adresselinje 2" value={item.adresse.adresselinje2} />*/}
								{/*		<TitleValue*/}
								{/*			title="Postnummer og -sted"*/}
								{/*			value={item.adresse.postnummer + ' ' + item.adresse.poststedsnavn}*/}
								{/*		/>*/}
								{/*		<TitleValue*/}
								{/*			title="Land"*/}
								{/*			value={item.adresse.landkode}*/}
								{/*			kodeverk={AdresseKodeverk.PostadresseLand}*/}
								{/*		/>*/}
								{/*	</Fragment>*/}
								{/*)}*/}
							</div>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
