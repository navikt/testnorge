import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Boadresse } from './Boadresse'

export const Partner = ({ data }) => {
	if (!data) return false

	return (
		<div>
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} />
				<TitleValue title="Fornavn" value={data.fornavn} />
				<TitleValue title="Mellomnavn" value={data.mellomnavn} />
				<TitleValue title="Etternavn" value={data.etternavn} />
				<TitleValue title="Kjønn" value={Formatters.kjonn(data.kjonn, data.alder)} />
				<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
				<TitleValue title="Diskresjonskode" value={Formatters.showLabel(data.spesreg)} />
				<TitleValue title="Uten fast bopel" value={data.utenFastBopel && 'Ja'} />
				{!data.utenFastBopel && <Boadresse boadresse={data.boadresse} visKunAdresse={true} />}
			</div>

			<div>
				<h4>Forhold</h4>
				{data.sivilstander.map((forhold, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
						<TitleValue
							title="Forhold til partner (sivilstand)"
							kodeverk="Sivilstander"
							value={forhold.sivilstand}
							size="medium"
						/>
						<TitleValue
							title="Sivilstand fra dato"
							value={Formatters.formatDate(forhold.sivilstandRegdato)}
						/>
					</div>
				))}
			</div>
		</div>
	)
}
