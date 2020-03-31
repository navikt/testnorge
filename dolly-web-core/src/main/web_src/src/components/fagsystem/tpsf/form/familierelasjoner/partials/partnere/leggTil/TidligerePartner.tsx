import * as React from 'react'
import { get as _get } from 'lodash'
import { head as _head } from 'lodash'
import { has as _has } from 'lodash'
import { last as _last } from 'lodash'
// @ts-ignore
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { nesteGyldigStatuser } from '../SivilstandOptions'

interface TidligerePartner {
	sistePartner: any
	formikBag: any
	sisteSivilstand: SivilstandObj
}

type SivilstandObj = {
	sivilstand: string
	sivilstandRegdato: string | null
}

export default ({ sistePartner, sisteSivilstand, formikBag }: TidligerePartner) => {
	if (!sistePartner || !sisteSivilstand) return null

	return (
		<>
			<h4>
				{sistePartner.fornavn} {sistePartner.etternavn} ({sistePartner.ident})
			</h4>
			<div className="flexbox" title="Du kan kun legge til nye sivilstander">
				<DollySelect
					name="sivilstand"
					label="Sivilstand"
					options={nesteGyldigStatuser('ALLE')}
					value={sisteSivilstand.sivilstand}
					disabled={true}
					fastfield={false}
				/>
				<DollyDatepicker
					value={sisteSivilstand.sivilstandRegdato}
					label="Sivilstand fra dato"
					disabled={true}
				/>
			</div>
		</>
	)
}
