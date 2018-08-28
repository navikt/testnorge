export default {
	services: {
		dollyBackend: process.env.dollyBackend || '/external/dolly/api/v1',
		tpsf: process.env.tpsf || '/external/tpsf/api/v1'
	}
}
