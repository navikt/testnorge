import { Diagnoser } from './Diagnoser'

export const SelectOptionsDiagnoser = () => {
	return Diagnoser.ICPC2.map(diagnose => ({
		value: diagnose.code,
		label: `${diagnose.code} - ${diagnose.text}`,
		diagnoseNavn: diagnose.text
	}))
}
