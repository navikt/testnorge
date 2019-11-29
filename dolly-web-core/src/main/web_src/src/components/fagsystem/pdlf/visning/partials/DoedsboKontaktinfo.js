import React, { Fragment } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import Formatters from '~/utils/DataFormatter'
import { Personnavn } from './Personnavn'
import { Adressat } from './Adressat'

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
							<Adressat adressat={id.adressat} />
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
							<TitleValue
								title="Dato Utstedt"
								value={Formatters.formatStringDates(id.utstedtDato)}
							/>
							<TitleValue title="Gyldig Fra" value={Formatters.formatStringDates(id.gyldigFom)} />
						</div>
					)))
				}
			</div>
		</div>
	)
}
