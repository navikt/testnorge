import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Adressevisning } from './Boadresse'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { PostadresseVisning } from './Postadresse'

import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const Partner = ({ data }) => {
	if (!data) return null

	return (
		<React.Fragment>
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} />
				<TitleValue title="Fornavn" value={data.fornavn} />
				<TitleValue title="Mellomnavn" value={data.mellomnavn} />
				<TitleValue title="Etternavn" value={data.etternavn} />
				<TitleValue title="Kjønn" value={Formatters.kjonn(data.kjonn, data.alder)} />
				<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
				<TitleValue title="Dødsdato" value={Formatters.formatDate(data.doedsdato)} />
				<TitleValue
					title="Diskresjonskode"
					kodeverk={PersoninformasjonKodeverk.Diskresjonskoder}
					value={data.spesreg}
				/>
				<TitleValue title="Uten fast bopel" value={data.utenFastBopel && 'Ja'} />
				{data.boadresse.length > 0 && (
					<Historikk component={Adressevisning} propName="boadresse" data={data.boadresse} />
				)}
				{data.postadresse.length > 0 && (
					<Historikk
						component={PostadresseVisning}
						propName="postadresse"
						data={data.postadresse}
					/>
				)}
			</div>
			{data.sivilstander.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray header="Forhold" data={data.sivilstander} nested>
						{(forhold, idx) => (
							<div key={idx} className="person-visning_content">
								<TitleValue
									title="Forhold til partner (sivilstand)"
									kodeverk={PersoninformasjonKodeverk.Sivilstander}
									value={forhold.sivilstand}
									size="medium"
								/>
								<TitleValue
									title="Sivilstand fra dato"
									value={Formatters.formatDate(forhold.sivilstandRegdato)}
								/>
							</div>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</React.Fragment>
	)
}
