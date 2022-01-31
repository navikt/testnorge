import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Oppholdsadresse } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import Formatters from '~/utils/DataFormatter'

type PdlOppholdsadresseProps = {
	data: Array<Oppholdsadresse>
}

type AdresseProps = {
	data: Oppholdsadresse
	idx?: number
}

const Adresse = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			{data.vegadresse && <Vegadresse adresse={data} idx={idx} />}
			{data.matrikkeladresse && <Matrikkeladresse adresse={data} idx={idx} />}
			{data.utenlandskAdresse && <UtenlandskAdresse adresse={data} idx={idx} />}
			{data.oppholdAnnetSted && (
				<div className="person-visning_content" key={idx}>
					<TitleValue
						title="Opphold annet sted"
						value={Formatters.showLabel('oppholdAnnetSted', data.oppholdAnnetSted)}
					/>
				</div>
			)}
		</div>
	)
}

export const PdlOppholdsadresse = ({ data }: PdlOppholdsadresseProps) => {
	if (!data || data.length === 0) return null

	const gyldigeAdresser = data.filter((adresse: Oppholdsadresse) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter((adresse: Oppholdsadresse) => adresse.metadata?.historisk)

	return (
		<>
			<SubOverskrift label="Oppholdsadresse" iconKind="adresse" />
			<ArrayHistorikk
				component={Adresse}
				data={gyldigeAdresser}
				historiskData={historiskeAdresser}
				header={''}
			/>
		</>
	)
}
