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
				{data.map((id, idx) => (
					<div className="person-visning_content" key={idx}>
						{id.personSomKontakt && (
							<Fragment>
								{id.personSomKontakt.personnavn && (
									<Personnavn data={id.personSomKontakt.personnavn} />
								)}
								<TitleValue title="Fødselsdato" value={id.personSomKontakt.foedselsdato} />
								<TitleValue
									title="Identifikasjonsnummer"
									value={id.personSomKontakt.identifikasjonsnummer}
								/>
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
								{id.advokatSomKontakt.personnavn && (
									<Personnavn data={id.advokatSomKontakt.personnavn} />
								)}
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
								{id.organisasjonSomKontakt.kontaktperson && (
									<Personnavn data={id.organisasjonSomKontakt.kontaktperson} />
								)}
							</Fragment>
						)}
						{id.adresse && (
							<Fragment>
								<TitleValue title="Adresselinje 1" value={id.adresse.adresselinje1} />
								<TitleValue title="Adresselinje 2" value={id.adresse.adresselinje2} />
								<TitleValue title="Landkode" value={id.adresse.landkode} />
								<TitleValue title="Postnummer" value={id.adresse.postnummer} />
								<TitleValue title="Poststedsnavn" value={id.adresse.poststedsnavn} />
							</Fragment>
						)}
						<TitleValue title="Skifteform" value={id.skifteform} />
					</div>
				))}
			</div>
		</div>
	)
}
