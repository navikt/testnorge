import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatAlder, formatDate, formatKjonn } from '@/utils/DataFormatter'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Adressevalg } from '@/components/fagsystem/tpsf/visning/partials/Adressevalg'
import React from 'react'

export const Partner = ({ data }) => {
	if (!data) {
		return null
	}

	const SivilstandVisning = ({ forhold, idx }) => {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Forhold til partner (sivilstand)"
					kodeverk={PersoninformasjonKodeverk.Sivilstander}
					value={forhold?.sivilstand}
					size="medium"
				/>
				<TitleValue title="Sivilstand fra dato" value={formatDate(forhold?.sivilstandRegdato)} />
			</div>
		)
	}

	return (
		<React.Fragment>
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} visKopier />
				<TitleValue title="Fornavn" value={data.fornavn} />
				<TitleValue title="Mellomnavn" value={data.mellomnavn} />
				<TitleValue title="Etternavn" value={data.etternavn} />
				<TitleValue title="Kjønn" value={formatKjonn(data.kjonn, data.alder)} />
				<TitleValue title="Alder" value={formatAlder(data.alder, data.doedsdato)} />
				<TitleValue title="Dødsdato" value={formatDate(data.doedsdato)} />
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
