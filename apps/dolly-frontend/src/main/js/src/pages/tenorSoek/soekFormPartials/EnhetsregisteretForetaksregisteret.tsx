import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import React, { SyntheticEvent } from 'react'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'

export const EnhetsregisteretForetaksregisteret = ({ handleChangeList }: any) => {
	const { domain: rollerOptions } = useTenorDomain('Roller')
	return (
		<SoekKategori>
			<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
				<FormSelect
					name="roller"
					options={createOptions(rollerOptions?.data)}
					isMulti={true}
					size="grow"
					label="Roller"
					onChange={(val: SyntheticEvent) => handleChangeList(val || null, 'roller')}
				/>
			</div>
		</SoekKategori>
	)
}
