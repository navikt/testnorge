import { runningE2ETest } from '@/service/services/Request'

export const initialPensjonGenerertInntekt = {
	generer: {
		fomAar: null,
		averageG: 1.5,
		tillatInntektUnder1G: false,
	},
}

export const initialPensjonInntekt = {
	fomAar: runningE2ETest() ? new Date().getFullYear() - 10 : null,
	tomAar: runningE2ETest() ? new Date().getFullYear() : null,
	belop: runningE2ETest() ? '12345' : '',
	redusertMedGrunnbelop: true,
}
