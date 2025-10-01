import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import React from 'react'

export const FolkeregisteretHendelser = ({ handleChange }: any) => {
	const { domain: hendelseOptions, loading: loadingHendelse } = useTenorDomain('Hendelse')

	return (
		<SoekKategori>
			<FormSelect
				name="hendelser.hendelse"
				options={createOptions(hendelseOptions?.data, true)}
				size="large"
				label="Har hatt hendelse"
				onChange={(val: any) =>
					handleChange(val?.value || null, 'hendelser.hendelse', `Har hatt hendelse: ${val?.label}`)
				}
				isLoading={loadingHendelse}
			/>
			<FormSelect
				name="hendelser.sisteHendelse"
				options={createOptions(hendelseOptions?.data, true)}
				size="large"
				label="Siste hendelse"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'hendelser.sisteHendelse',
						`Siste hendelse: ${val?.label}`,
					)
				}
				isLoading={loadingHendelse}
			/>
		</SoekKategori>
	)
}
