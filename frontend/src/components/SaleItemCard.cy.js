import SaleItemCard from './SaleItemCard.vue'

describe('<SaleItemCard />', () => {
  it('renders', () => {
    // see: https://on.cypress.io/mounting-vue
    cy.mount(SaleItemCard)
  })
})