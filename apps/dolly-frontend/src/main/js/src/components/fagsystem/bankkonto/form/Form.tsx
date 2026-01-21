import { NorskBankkonto } from './NorskBankkonto'
import { UtenlandskBankkonto } from './UtenlandskBankkonto'
import { useFormContext } from 'react-hook-form'
import { bankkontoValidation } from '@/components/fagsystem/bankkonto/form/validation'

const BankkontoForm = () => {
	const formMethods = useFormContext()
	return (
		<>
			<NorskBankkonto formMethods={formMethods} />
			<UtenlandskBankkonto formMethods={formMethods} />
		</>
	)
}

BankkontoForm.validation = bankkontoValidation

export default BankkontoForm
