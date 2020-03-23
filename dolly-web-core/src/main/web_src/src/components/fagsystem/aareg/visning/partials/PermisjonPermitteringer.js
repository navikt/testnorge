import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ArbeidKodeverk } from '~/config/kodeverk'

export const PermisjonPermitteringer = ({ data }) => {
	if (!data) return false

	return (
		<React.Fragment>
			<h4>Permisjon</h4>
			<DollyFieldArray data={data} nested>
				{(id, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Permisjonstype"
							value={id.type}
							kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
						/>
						{id.periode && (
							<TitleValue title="Startdato" value={Formatters.formatStringDates(id.periode.fom)} />
						)}
						{id.periode && (
							<TitleValue title="Sluttdato" value={Formatters.formatStringDates(id.periode.tom)} />
						)}
						<TitleValue title="Permisjonsprosent" value={id.prosent} />
					</div>
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}
