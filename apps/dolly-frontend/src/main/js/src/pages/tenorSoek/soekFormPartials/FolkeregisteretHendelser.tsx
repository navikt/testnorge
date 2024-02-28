import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React, { SyntheticEvent } from 'react'

export const FolkeregisteretHendelser = ({ handleChange }: any) => {
	const { domain: hendelseOptions } = useTenorDomain('Hendelse')

	return (
		<SoekKategori>
			<FormikSelect
				name="hendelser.hendelse"
				options={createOptions(hendelseOptions?.data)}
				size="xlarge"
				label="Har hatt hendelse"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'hendelser.hendelse')}
			/>
			<FormikSelect
				name="hendelser.sisteHendelse"
				options={createOptions(hendelseOptions?.data)}
				size="xlarge"
				label="Siste hendelse"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.value || null, 'hendelser.sisteHendelse')
				}
			/>
		</SoekKategori>
	)
}
