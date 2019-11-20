import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Personinfo = ({ data, visTittel = true }) => {
	return (
		<div>
			{visTittel && <SubOverskrift label="Persondetaljer" />}
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} />
				<TitleValue title="Fornavn" value={data.fornavn} />

				<TitleValue title="Mellomnavn" value={data.mellomnavn} />

				<TitleValue title="Etternavn" value={data.etternavn} />

				<TitleValue title="Kjønn" value={Formatters.kjonnToString(data.kjonn)} />
				<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />

				<TitleValue title="Personstatus" kodeverk="Personstatuser" value={data.personStatus} />

				<TitleValue title="Savnet siden" value={Formatters.formatDate(data.forsvunnetDato)} />

				<TitleValue title="Sivilstand" kodeverk="Sivilstander" value={data.sivilstand} />

				<TitleValue title="Diskresjonskoder" kodeverk="Diskresjonskoder" value={data.spesreg} />

				<TitleValue
					title="Uten fast bopel"
					value={Formatters.oversettBoolean(data.utenFastBopel)}
				/>

				{data.gtVerdi && (
					<TitleValue
						title="Geo. tilhør."
						kodeverk={Formatters.gtApiKodeverkId(data.gtType)}
						value={data.gtVerdi}
						size="medium"
					>
						{value => `${Formatters.gtTypeLabel(data.gtType)} - ${value.label}`}
					</TitleValue>
				)}

				<TitleValue
					title="TK-nummer"
					value={data.tknavn ? `${data.tknr} - ${data.tknavn}` : data.tknr}
					size="medium"
				/>
				{data.egenAnsattDatoFom && <TitleValue title="Egenansatt" value="JA" />}
			</div>
		</div>
	)
}
