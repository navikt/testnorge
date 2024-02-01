import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React, { SyntheticEvent } from 'react'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'

export const EnhetsregisteretForetaksregisteret = ({
	formikBag,
	handleChangeList,
	getValue,
}: any) => {
	const { domain: rollerOptions } = useTenorDomain('Roller')
	return (
		<SoekKategori>
			<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
				<FormikSelect
					name="roller"
					options={createOptions(rollerOptions?.data)}
					isMulti={true}
					size="grow"
					label="Roller"
					onChange={(val: SyntheticEvent) => handleChangeList(val || null, 'roller')}
					value={getValue('roller')}
				/>
			</div>
		</SoekKategori>
	)
}
