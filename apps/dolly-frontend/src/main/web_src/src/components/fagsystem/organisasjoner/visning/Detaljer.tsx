import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { OrganisasjonKodeverk } from '~/config/kodeverk'
import '~/pages/gruppe/PersonVisning/PersonVisning.less'
import { EnhetData } from '../types'
import Formatters from '~/utils/DataFormatter'
import AdresseDetaljer from './AdresseDetaljer'
import KontaktdataDetaljer from './KontaktdataDetaljer'

type Detaljer = {
	data: Array<EnhetData>
}

export const Detaljer = ({ data }: Detaljer) => {
	return (
		<section className="person-visning">
			<SubOverskrift label="Detaljer" iconKind="personinformasjon" />
			<div className="person-visning_content">
				<TitleValue title="Orgnr." value={data[0].organisasjonsnummer} />
				<TitleValue title="Navn" value={data[0].organisasjonsnavn} />
				<TitleValue title="Enhetstype" value={data[0].enhetstype} />
				<TitleValue
					title="Næringskode"
					kodeverk={OrganisasjonKodeverk.Naeringskoder}
					value={data[0].naeringskode}
				/>
				<TitleValue
					title="Sektorkode"
					kodeverk={OrganisasjonKodeverk.Sektorkoder}
					value={data[0].sektorkode}
				/>
				<TitleValue title="Formål" value={data[0].formaal} />
				<TitleValue
					title="Stiftelsesdato"
					value={Formatters.formatStringDates(data[0].stiftelsesdato)}
				/>
				<TitleValue title="Målform" value={Formatters.showLabel('maalform', data[0].maalform)} />
			</div>

			{(data[0].telefon || data[0].epost || data[0].nettside) && (
				<KontaktdataDetaljer data={data[0]} />
			)}

			{data[0].adresser && (
				<>
					<h4>Adresser</h4>
					<div className="person-visning_content">
						{data[0].adresser.map((adresse, idx) => (
							<AdresseDetaljer adresse={adresse} key={idx} />
						))}
					</div>
				</>
			)}
		</section>
	)
}
