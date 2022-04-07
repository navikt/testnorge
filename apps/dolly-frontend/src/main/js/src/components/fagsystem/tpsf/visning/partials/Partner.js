import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Adressevalg } from '~/components/fagsystem/tpsf/visning/partials/Adressevalg'

export const Partner = ({ data }) => {
	if (!data) return null

	const SivilstandVisning = ({ forhold, idx }) => {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Forhold til partner (sivilstand)"
					kodeverk={PersoninformasjonKodeverk.Sivilstander}
					value={forhold?.sivilstand}
					size="medium"
				/>
				<TitleValue
					title="Sivilstand fra dato"
					value={Formatters.formatDate(forhold?.sivilstandRegdato)}
				/>
			</div>
		)
	}

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
				<Adressevalg data={data} />
			</div>
			{data.sivilstander?.length > 0 ? (
				<ErrorBoundary>
					<DollyFieldArray header="Forhold" data={data.sivilstander} nested>
						{(forhold, idx) => <SivilstandVisning forhold={forhold} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			) : (
				<SivilstandVisning forhold={data.sivilstand} />
			)}
		</React.Fragment>
	)
}
