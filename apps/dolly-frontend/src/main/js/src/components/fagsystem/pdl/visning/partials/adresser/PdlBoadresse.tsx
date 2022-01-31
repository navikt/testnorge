import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { BostedData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'

type PdlBoadresseProps = {
	data: Array<BostedData>
}

type AdresseProps = {
	data: BostedData
	idx?: number
}

const Adresse = ({ data, idx }: AdresseProps) => {
	return (
		<div className="person-visning_content">
			{data.vegadresse && <Vegadresse adresse={data} idx={idx} />}
			{data.matrikkeladresse && <Matrikkeladresse adresse={data} idx={idx} />}
			{data.utenlandskAdresse && <UtenlandskAdresse adresse={data} idx={idx} />}
			{data.ukjentBosted && (
				<>
					<h4 style={{ marginTop: '0px' }}>Ukjent bosted</h4>
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Bostedskommune"
							value={data.ukjentBosted.bostedskommune || 'Ikke oppgitt'}
						/>
					</div>
				</>
			)}
		</div>
	)
}

export const PdlBoadresse = ({ data }: PdlBoadresseProps) => {
	if (!data || data.length === 0) return null

	const gyldigeAdresser = data.filter((adresse: BostedData) => !adresse.metadata?.historisk)
	const historiskeAdresser = data.filter((adresse: BostedData) => adresse.metadata?.historisk)

	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<ArrayHistorikk
				component={Adresse}
				data={gyldigeAdresser}
				historiskData={historiskeAdresser}
				header={''}
			/>
		</>
	)
}
